const API = {
    baseURL: '/api',
    
    async get(endpoint) {
        const res = await fetch(`${this.baseURL}${endpoint}`);
        return res.json();
    },
    
    async post(endpoint, data) {
        const res = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });
        return res.json();
    },
    
    async put(endpoint, data) {
        const res = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });
        return res.json();
    },
    
    async delete(endpoint) {
        const res = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'DELETE'
        });
        return res.json();
    },
    
    async postFormData(endpoint, formData) {
        const res = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'POST',
            body: formData
        });
        return res.json();
    },
	
	async putFormData(endpoint, formData) {
	    const res = await fetch(`${this.baseURL}${endpoint}`, {
	        method: 'PUT',
	        body: formData
	    });
	    return res.json();
	}
};