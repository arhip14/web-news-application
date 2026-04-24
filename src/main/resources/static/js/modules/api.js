export const API = {
    async fetchNews(headers = {}) {
        const res = await fetch('/api/news', { headers });
        return res.ok ? await res.json() : [];
    },
    async fetchCategories() {
        const res = await fetch('/api/categories');
        return res.ok ? await res.json() : [];
    }
};
