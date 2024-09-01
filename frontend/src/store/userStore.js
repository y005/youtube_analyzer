import {createStore} from "vuex";

export const userStore = createStore({
    state: () => {
        return {
            token: ""
        }
    },
    getters: {
        getToken() {
            return this.token;
        }
    },
    mutations: {
        setToken(token) {
          this.token = token
        }
    },
    actions: {
        saveToken(token) {
            this.setToken(token)
        }
    },
})