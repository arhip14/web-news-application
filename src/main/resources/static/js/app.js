import { API } from './modules/api.js';

let currentUser = JSON.parse(localStorage.getItem('user'));
let profileData = null;
window.currentFilter = null;
window.allNews = [];

const getAuthHeaders = () => {
    if (!currentUser || !currentUser.header) return {};
    return { 'Authorization': currentUser.header };
};

async function init() {
    console.log("🚀 KPI News System Initializing...");

    if (window.location.pathname.includes('auth.html')) return;

    if (currentUser && currentUser.header) {
        await fetchUserProfile();
    }

    updateUI();
    await loadCategories();
    await fetchNews();
    bindEvents();
}

async function fetchUserProfile() {
    try {
        const res = await fetch('/api/auth/me', { headers: getAuthHeaders() });
        if (res.ok) {
            profileData = await res.json();
        } else if (res.status === 401 || res.status === 403) {
            localStorage.removeItem('user');
            currentUser = null;
        }
    } catch (e) { console.error("Profile Fetch Error", e); }
}

function updateUI() {
    const authNav = document.getElementById('authNav');
    const adminSection = document.getElementById('adminSection');
    const userManagerArea = document.getElementById('userManagerArea');
    const adminDivider = document.getElementById('adminDivider');

    if (!authNav) return;

    if (currentUser && profileData) {
        const role = (profileData.role || "").toUpperCase();
        const isAuthor = role === 'CREATOR' || role === 'ADMIN';
        const isAdmin = role === 'ADMIN';

        if (adminSection) adminSection.style.display = isAuthor ? 'block' : 'none';

        if (isAdmin && userManagerArea) {
            userManagerArea.style.display = 'block';
            adminDivider.style.display = 'block';
            loadAdminUsers();
        }

        const avatar = profileData.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(profileData.fullName)}&background=f0f0f2&color=007AFF`;

        authNav.innerHTML = `
            <div class="profile-chip" onclick="window.openProfileModal()">
                <img src="${avatar}" class="nav-avatar">
                <div class="profile-text">
                    <span class="nav-name">${profileData.fullName}</span>
                    <span class="nav-role">${role === 'CREATOR' ? 'Автор' : (role === 'ADMIN' ? 'Адміністратор' : 'Читач')}</span>
                </div>
                <button id="logoutBtn" class="btn-logout-icon" onclick="event.stopPropagation(); window.logout();">✕</button>
            </div>`;
    } else {
        if (adminSection) adminSection.style.display = 'none';
        authNav.innerHTML = `<a href="/auth.html" class="btn-send" style="text-decoration:none">Увійти</a>`;
    }
}

// --- АДМІНІСТРАТОР: КЕРУВАННЯ КОРИСТУВАЧАМИ ---
async function loadAdminUsers() {
    const tableBody = document.getElementById('adminUserTable');
    if (!tableBody) return;

    try {
        const res = await fetch('/api/admin/users', { headers: getAuthHeaders() });
        if (res.ok) {
            const users = await res.json();
            tableBody.innerHTML = users.map(user => `
                <tr>
                    <td><strong>${user.fullName}</strong></td>
                    <td>${user.email}</td>
                    <td>
                        <select class="admin-select" onchange="window.updateUserRole(${user.id}, this.value)">
                            <option value="READER" ${user.role === 'READER' ? 'selected' : ''}>Читач</option>
                            <option value="CREATOR" ${user.role === 'CREATOR' ? 'selected' : ''}>Автор</option>
                            <option value="ADMIN" ${user.role === 'ADMIN' ? 'selected' : ''}>Адмін</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn-mini-delete" onclick="window.deleteUser(${user.id})">Видалити</button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) { console.error("Admin Load Error", e); }
}

window.updateUserRole = async (userId, newRole) => {
    const res = await fetch(`/api/admin/users/${userId}/role?role=${newRole}`, {
        method: 'PUT',
        headers: getAuthHeaders()
    });
    if (res.ok) {
        alert('Роль змінена успішно!');
        if (userId === profileData.id) window.location.reload();
    }
};

window.deleteUser = async (userId) => {
    if (confirm('Видалити користувача?')) {
        const res = await fetch(`/api/admin/users/${userId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (res.ok) loadAdminUsers();
    }
};

// --- НОВИНИ ТА КАТЕГОРІЇ ---
async function loadCategories() {
    const select = document.getElementById('newsCategory');
    const menu = document.getElementById('categoryMenu');
    if (!select || !menu) return;
    try {
        const categories = await API.fetchCategories();
        select.innerHTML = categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        menu.innerHTML = `
            <button class="category-tag-btn" onclick="window.filterByCategory(null)">Всі новини</button>
            ${categories.map(c => `<button class="category-tag-btn" onclick="window.filterByCategory(${c.id})">${c.name}</button>`).join('')}
        `;
    } catch (e) { console.error(e); }
}

async function fetchNews() {
    try {
        const news = await API.fetchNews(getAuthHeaders());
        window.allNews = news || [];
        renderNews(window.allNews);
    } catch (e) { console.error(e); }
}

function renderNews(news) {
    const grid = document.getElementById('newsGrid');
    if (!grid) return;
    grid.innerHTML = news.map(item => `
        <article class="news-card">
            <img src="${item.imageUrl || 'https://placehold.co/600x400/eeeeee/999999?text=KPI+NEWS'}" class="news-image">
            <div class="news-content">
                <span class="category-tag" onclick="window.filterByCategory(${item.categoryId})">${item.categoryName || 'Новини'}</span>
                <h3>${item.title}</h3>
                <p>${item.content.substring(0, 150)}...</p>
                <div class="card-meta">
                    <span>Автор: ${item.authorEmail.split('@')[0]}</span>
                    ${(profileData?.role === 'ADMIN' || (profileData?.role === 'CREATOR' && currentUser?.email === item.authorEmail))
        ? `<button class="btn-mini-delete" onclick="window.deleteNews(${item.id})">Видалити</button>` : ''}
                </div>
                <button class="btn-comment-toggle" onclick="window.toggleComments(${item.id})">💬 Обговорення</button>
            </div>
            <div class="comments-section" id="comments-${item.id}" style="display:none;">
                <div class="comments-list" id="comments-list-${item.id}"></div>
                ${currentUser ? `
                <div class="comment-input-group">
                    <input type="text" id="comment-input-${item.id}" placeholder="Напишіть коментар...">
                    <button class="btn-send" onclick="window.submitComment(${item.id})">OK</button>
                </div>` : '<p style="font-size:12px; color:#777; margin-top:10px;">Увійдіть, щоб коментувати</p>'}
            </div>
        </article>
    `).reverse().join('');
}

function bindEvents() {
    document.getElementById('publishBtn').onclick = publish;
    document.getElementById('searchInput').oninput = (e) => {
        const query = e.target.value.toLowerCase();
        renderNews(window.allNews.filter(n => n.title.toLowerCase().includes(query) || n.content.toLowerCase().includes(query)));
    };
}

async function publish() {
    const title = document.getElementById('newsTitle').value;
    const content = document.getElementById('newsContent').value;
    const categoryId = document.getElementById('newsCategory').value;
    const imageUrl = document.getElementById('newsImageUrl').value;
    if (!title || !content) return alert("Заповніть поля!");
    const res = await fetch('/api/news', {
        method: 'POST',
        headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, content, categoryId: parseInt(categoryId), imageUrl })
    });
    if (res.ok) window.location.reload();
}

window.deleteNews = async (id) => {
    if (confirm('Видалити новину?')) {
        const res = await fetch(`/api/news/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders() // ДОДАНО: Для усунення 403
        });
        if (res.ok) fetchNews();
        else alert('Помилка видалення. Перевірте роль або перезайдіть в акаунт.');
    }
};

// --- КОМЕНТАРІ З ВІДПОВІДЯМИ ---
window.toggleComments = async (id) => {
    const s = document.getElementById(`comments-${id}`);
    s.style.display = s.style.display === 'none' ? 'block' : 'none';
    if (s.style.display === 'block') await window.loadComments(id);
};

window.loadComments = async (newsId) => {
    const list = document.getElementById(`comments-list-${newsId}`);
    list.innerHTML = '<span style="font-size:12px; color:#888;">Завантаження...</span>';
    try {
        const res = await fetch(`/api/comments/news/${newsId}`);
        if (res.ok) {
            const comments = await res.json();
            if (comments.length === 0) {
                list.innerHTML = '<p style="font-size:12px; color:#888;">Коментарів ще немає.</p>';
                return;
            }

            const commentsMap = {};
            const rootComments = [];
            comments.forEach(c => {
                c.replies = [];
                commentsMap[c.id] = c;
            });
            comments.forEach(c => {
                if (c.parentId && commentsMap[c.parentId]) commentsMap[c.parentId].replies.push(c);
                else rootComments.push(c);
            });

            const renderTree = (commentList, level = 0) => {
                return commentList.map(c => `
                    <div style="padding:10px; border-left:${level > 0 ? '2px solid var(--primary)' : 'none'}; margin-left:${level * 20}px; background:${level === 0 ? '#fcfcfc' : 'transparent'}; margin-bottom:5px; border-bottom:1px solid #eee;">
                        <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
                            <strong style="font-size:13px; color:var(--dark);">${c.authorName}</strong>
                            <span style="font-size:10px; color:#999;">${new Date(c.createdAt).toLocaleString()}</span>
                        </div>
                        <p style="margin:0 0 5px 0; font-size:14px; color:var(--text-main);">${c.text}</p>
                        ${currentUser ? `
                            <button style="background:none; border:none; color:var(--primary); font-size:11px; cursor:pointer; padding:0; font-weight:700;" onclick="window.showReplyBox(${c.id})">ВІДПОВІСТИ</button>
                            <div id="reply-box-${c.id}" style="display:none; margin-top:8px; gap:5px;">
                                <input type="text" id="reply-input-${c.id}" placeholder="Ваша відповідь..." style="flex:1; padding:6px; font-size:12px; border:1px solid var(--border);">
                                <button class="btn-send" style="padding:4px 8px; font-size:10px;" onclick="window.submitComment(${newsId}, ${c.id})">OK</button>
                            </div>
                        ` : ''}
                        <div style="margin-top:5px;">${renderTree(c.replies, level + 1)}</div>
                    </div>
                `).join('');
            };
            list.innerHTML = renderTree(rootComments);
        }
    } catch(e) { list.innerHTML = 'Помилка завантаження'; }
};

window.showReplyBox = (id) => {
    const box = document.getElementById(`reply-box-${id}`);
    box.style.display = box.style.display === 'none' ? 'flex' : 'none';
};

window.submitComment = async (newsId, parentId = null) => {
    const inputId = parentId ? `reply-input-${parentId}` : `comment-input-${newsId}`;
    const input = document.getElementById(inputId);
    const text = input.value.trim();
    if (!text) return;

    const res = await fetch('/api/comments', {
        method: 'POST',
        headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' },
        body: JSON.stringify({ newsId, text, parentId })
    });

    if (res.ok) {
        input.value = '';
        await window.loadComments(newsId);
    } else { alert('Помилка при додаванні коментаря'); }
};

// --- ПРОФІЛЬ ---
window.openProfileModal = () => {
    document.getElementById('editFullName').value = profileData.fullName;
    document.getElementById('editBio').value = profileData.bio;
    document.getElementById('editAvatarUrl').value = profileData.avatarUrl;
    document.getElementById('previewAvatar').src = profileData.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(profileData.fullName)}`;
    document.getElementById('profileRoleBadge').innerText = profileData.role;
    document.getElementById('profileModal').style.display = 'flex';
};

window.closeProfileModal = () => document.getElementById('profileModal').style.display = 'none';

window.saveProfile = async () => {
    const res = await fetch('/api/auth/profile', {
        method: 'PUT',
        headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' },
        body: JSON.stringify({
            fullName: document.getElementById('editFullName').value,
            bio: document.getElementById('editBio').value,
            avatarUrl: document.getElementById('editAvatarUrl').value
        })
    });
    if (res.ok) window.location.reload();
};

window.uploadAvatarFile = async (input) => {
    const fd = new FormData(); fd.append('file', input.files[0]);
    const res = await fetch('/api/upload/image', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: fd
    });
    if (res.ok) {
        const url = await res.text();
        document.getElementById('editAvatarUrl').value = url;
        document.getElementById('previewAvatar').src = url;
    }
};

window.uploadNewsImage = async (input) => {
    const fd = new FormData(); fd.append('file', input.files[0]);
    const res = await fetch('/api/upload/image', {
        method: 'POST',
        headers: getAuthHeaders(),
        body: fd
    });
    if (res.ok) {
        const url = await res.text();
        document.getElementById('newsImageUrl').value = url;
        document.getElementById('previewNewsImage').src = url;
    }
};

window.generateRandomAvatar = () => {
    const url = `https://api.dicebear.com/7.x/adventurer/svg?seed=${Math.random()}`;
    document.getElementById('editAvatarUrl').value = url;
    document.getElementById('previewAvatar').src = url;
};

window.filterByCategory = (id) => {
    window.currentFilter = (window.currentFilter === id) ? null : id;
    renderNews(window.currentFilter ? window.allNews.filter(n => n.categoryId == window.currentFilter) : window.allNews);
};

window.logout = () => { localStorage.removeItem('user'); window.location.reload(); };

document.addEventListener('DOMContentLoaded', init);