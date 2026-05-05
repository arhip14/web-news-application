export const Auth = {
    async register(userData) {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        return response;
    },

    getAuthHeader(email, password) {
        const credentials = email + ':' + password;
        // Виправляємо баг з кирилицею: спочатку кодуємо в UTF-8, потім в Base64
        const encodedCredentials = btoa(unescape(encodeURIComponent(credentials)));
        return 'Basic ' + encodedCredentials;
    }
};