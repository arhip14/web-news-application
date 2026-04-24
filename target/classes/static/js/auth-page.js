import { Auth } from './modules/auth.js';

let selectedRole = 'READER';

document.addEventListener('DOMContentLoaded', () => {
    // Ініціалізація подій
    initTabSystem();
    initRoleSystem();

    // Кнопки відправки
    document.getElementById('btnLogin').onclick = handleLogin;
    document.getElementById('btnRegister').onclick = handleRegister;
});

function initTabSystem() {
    const tabs = {
        'tabLogin': 'login',
        'tabReg': 'reg'
    };

    Object.entries(tabs).forEach(([id, type]) => {
        document.getElementById(id).onclick = () => {
            // Прибираємо активні класи у всіх
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.form-group').forEach(g => g.classList.remove('active'));

            // Додаємо активному
            document.getElementById(id).classList.add('active');
            document.getElementById(`${type}Group`).classList.add('active');
        };
    });
}

function initRoleSystem() {
    const roles = ['READER', 'CREATOR'];
    roles.forEach(role => {
        const btn = document.getElementById(`btn${role.charAt(0) + role.slice(1).toLowerCase()}`);
        if (btn) {
            btn.onclick = () => {
                selectedRole = role;
                document.getElementById('btnReader').classList.toggle('active', role === 'READER');
                document.getElementById('btnCreator').classList.toggle('active', role === 'CREATOR');
                console.log(`🎯 Обрана роль: ${selectedRole}`);
            };
        }
    });
}

async function handleLogin() {
    const email = document.getElementById('loginEmail').value;
    const pass = document.getElementById('loginPass').value;

    if (!email || !pass) return alert('Заповніть пошта та пароль');

    const header = Auth.getAuthHeader(email, pass);

    try {
        // Перевіряємо вхід через запит до профілю (це найнадійніше)
        const res = await fetch('/api/auth/me', {
            headers: { 'Authorization': header }
        });

        if (res.ok) {
            const profile = await res.json();
            localStorage.setItem('user', JSON.stringify({ email, header }));
            console.log(`✅ Вітаємо, ${profile.fullName}`);
            window.location.replace('/'); // replace замість href, щоб не вертатися назад кнопкою "Back"
        } else {
            alert('Невірні дані для входу');
        }
    } catch (e) {
        alert('Сервер недоступний');
    }
}

async function handleRegister() {
    const userData = {
        fullName: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPass').value,
        bio: document.getElementById('regBio').value,
        role: selectedRole
    };

    if (!userData.fullName || !userData.email || !userData.password) {
        return alert('Будь ласка, заповніть обов’язкові поля');
    }

    try {
        const res = await Auth.register(userData);

        if (res.ok) {
            // Оскільки Auth.register вже зберігає дані в localStorage (ми це додали раніше),
            // просто перенаправляємо користувача
            window.location.replace('/');
        } else {
            alert('Помилка реєстрації. Можливо, цей Email вже використовується.');
        }
    } catch (e) {
        alert('Помилка з’єднання з сервером');
    }
}