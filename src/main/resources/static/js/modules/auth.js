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
        return 'Basic ' + btoa(email + ':' + password);
    }
};