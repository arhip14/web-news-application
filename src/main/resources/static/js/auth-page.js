import { Auth } from './modules/auth.js';

let selectedRole = 'READER';

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('tabLogin').onclick = () => switchTab('login');
    document.getElementById('tabReg').onclick = () => switchTab('reg');

    document.getElementById('btnReader').onclick = () => setRole('READER');
    document.getElementById('btnCreator').onclick = () => setRole('CREATOR');

    document.getElementById('btnLogin').onclick = handleLogin;
    document.getElementById('btnRegister').onclick = handleRegister;
});

function switchTab(tab) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.form-group').forEach(g => g.classList.remove('active'));

    if (tab === 'login') {
        document.getElementById('tabLogin').classList.add('active');
        document.getElementById('loginGroup').classList.add('active');
    } else {
        document.getElementById('tabReg').classList.add('active');
        document.getElementById('regGroup').classList.add('active');
    }
}

function setRole(role) {
    selectedRole = role;
    document.getElementById('btnReader').classList.toggle('active', role === 'READER');
    document.getElementById('btnCreator').classList.toggle('active', role === 'CREATOR');
}

async function handleLogin() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPass').value;

    if (!email || !password) return alert('Заповніть пошту та пароль');

    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (res.ok) {
            const data = await res.json();
            const token = `Bearer ${data.token}`;
            localStorage.setItem('user', JSON.stringify({ email: data.email, header: token }));
            window.location.replace('/');
        } else {
            const errorText = await res.text();
            console.error("Login failed:", errorText);
            alert('Помилка входу: перевірте логін та пароль або доступ до сервера');
        }
    } catch (e) {
        console.error("Network error:", e);
        alert('Сервер недоступний');
    }
}

async function handleRegister() {
    const avatarInput = document.getElementById('regAvatarUrl');

    const userData = {
        fullName: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPass').value,
        bio: document.getElementById('regBio').value,
        role: selectedRole,
        avatarUrl: avatarInput ? avatarInput.value : '' // Гарантовано беремо фото, якщо воно є
    };

    if (!userData.fullName || !userData.email || !userData.password) {
        return alert('Будь ласка, заповніть обов’язкові поля (Ім\'я, Email, Пароль)');
    }

    try {
        const res = await Auth.register(userData);
        if (res.ok) {
            alert('✅ Акаунт успішно створено! Будь ласка, увійдіть у систему.');

            switchTab('login');

            document.getElementById('loginEmail').value = userData.email;
            document.getElementById('loginPass').value = '';
        } else {
            const err = await res.text();
            alert(err || 'Помилка реєстрації. Можливо, цей Email вже використовується.');
        }
    } catch (e) {
        alert('Помилка з’єднання з сервером');
    }
}


window.generateRegAvatar = () => {
    const seed = Math.random().toString(36).substring(7);
    const randomUrl = `https://api.dicebear.com/7.x/adventurer/svg?seed=${seed}&backgroundColor=c0aede,b6e3f4,d1d4f9,ffd5dc,ffdfbf`;
    document.getElementById('regAvatarUrl').value = randomUrl;
    document.getElementById('regPreviewAvatar').src = randomUrl;
};

window.uploadRegAvatar = async (input) => {
    const file = input.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
        const res = await fetch('/api/upload/avatar', {
            method: 'POST',
            body: formData
        });

        if (res.ok) {
            const fileUrl = await res.text();
            const avatarUrlInput = document.getElementById('regAvatarUrl');
            if (avatarUrlInput) avatarUrlInput.value = fileUrl;


            const previewImg = document.getElementById('regAvatarPreview') || document.getElementById('regPreviewAvatar');
            if (previewImg) previewImg.src = fileUrl;

            console.log("✅ Аватар завантажено:", fileUrl);
        } else {
            const error = await res.text();
            alert('Помилка завантаження: ' + error);
        }
    } catch (e) {
        console.error("Upload error:", e);
        alert('Сервер недоступний');
    }
};