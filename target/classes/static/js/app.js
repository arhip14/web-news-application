import { API } from './modules/api.js';

let currentUser = JSON.parse(localStorage.getItem('user'));
let profileData = null;

const getAuthHeaders = () => {
    if (!currentUser || !currentUser.header) return {};
    return { 'Authorization': currentUser.header };
};

async function init() {
    console.log("🚀 KPI News System Initializing...");

    if (currentUser) {
        await fetchUserProfile();
    }

    updateUI();
    // Виправляємо помилку: функції тепер точно є в коді нижче
    await loadCategories();
    await fetchNews();
    bindEvents();
}

async function fetchUserProfile() {
    try {
        const res = await fetch('/api/auth/me', { headers: getAuthHeaders() });
        if (res.ok) {
            profileData = await res.json();
            console.log("✅ Profile loaded:", profileData.role);
        } else if (res.status === 401) {
            localStorage.removeItem('user');
            currentUser = null;
        }
    } catch (e) { console.error("Profile Fetch Error", e); }
}

function updateUI() {
    const authNav = document.getElementById('authNav');
    const adminSection = document.getElementById('adminSection');

    if (!authNav) return;

    if (currentUser && profileData) {
        const role = (profileData.role || "").toUpperCase();
        const isAuthor = role === 'CREATOR' || role === 'ADMIN';

        if (adminSection) {
            adminSection.classList.toggle('hidden', !isAuthor);
            adminSection.style.display = isAuthor ? 'block' : 'none';
        }

        const avatar = `https://ui-avatars.com/api/?name=${encodeURIComponent(profileData.fullName)}&background=f0f0f2&color=007AFF`;

        authNav.innerHTML = `
            <div class="profile-chip">
                <img src="${avatar}" class="nav-avatar">
                <div class="profile-text" style="display:flex; flex-direction:column">
                    <span class="nav-name">${profileData.fullName}</span>
                    <span class="nav-role">${role === 'CREATOR' ? 'Editor' : 'Reader'}</span>
                </div>
                <button id="logoutBtn" class="btn-logout-icon">✕</button>
            </div>`;
        document.getElementById('logoutBtn').onclick = logout;
    } else {
        if (adminSection) adminSection.style.display = 'none';
        authNav.innerHTML = `<a href="/auth.html" class="btn-send" style="text-decoration:none">Sign In</a>`;
    }
}

async function loadCategories() {
    const select = document.getElementById('newsCategory');
    if (!select) return;
    try {
        const categories = await API.fetchCategories();
        select.innerHTML = categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    } catch (e) { console.error("Categories Load Error", e); }
}

async function fetchNews() {
    try {
        const news = await API.fetchNews(getAuthHeaders());
        window.allNews = news || [];
        renderNews(window.allNews);
    } catch (e) { console.error("News Fetch Error", e); }
}

function renderNews(news) {
    const grid = document.getElementById('newsGrid');
    if (!grid) return;

    if (!news || news.length === 0) {
        grid.innerHTML = '<div class="no-news">Стрічка новин поки порожня...</div>';
        return;
    }

    grid.innerHTML = news.map(item => `
        <article class="news-card">
            <span class="category-tag">${item.categoryName || 'General'}</span>
            <h3>${item.title}</h3>
            <p>${item.content}</p>
            <div class="card-meta">
                <span>By <strong>${item.authorEmail ? item.authorEmail.split('@')[0] : 'Admin'}</strong></span>
                ${(profileData?.role === 'ADMIN' || (profileData?.role === 'CREATOR' && currentUser?.email === item.authorEmail))
        ? `<button class="btn-mini-delete" onclick="window.deleteNews(${item.id})">Remove</button>` : ''}
            </div>
        </article>
    `).reverse().join('');
}

function bindEvents() {
    const publishBtn = document.getElementById('publishBtn');
    if (publishBtn) publishBtn.onclick = publish;

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.oninput = (e) => {
            const query = e.target.value.toLowerCase();
            const filtered = window.allNews.filter(n =>
                n.title.toLowerCase().includes(query) || n.content.toLowerCase().includes(query)
            );
            renderNews(filtered);
        };
    }
}

async function publish() {
    const title = document.getElementById('newsTitle')?.value;
    const content = document.getElementById('newsContent')?.value;
    const categoryId = document.getElementById('newsCategory')?.value;

    if (!title || !content) return alert("Fill all fields");

    const res = await fetch('/api/news', {
        method: 'POST',
        headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, content, categoryId: parseInt(categoryId) })
    });

    if (res.ok) {
        document.getElementById('newsTitle').value = '';
        document.getElementById('newsContent').value = '';
        await fetchNews();
    } else { alert("Publish Error"); }
}

window.deleteNews = async (id) => {
    if (confirm('Delete this post?')) {
        const res = await fetch(`/api/news/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (res.ok) await fetchNews();
    }
};

function logout() {
    localStorage.removeItem('user');
    window.location.reload();
}

document.addEventListener('DOMContentLoaded', init);