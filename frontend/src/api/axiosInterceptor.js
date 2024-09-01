import axios from "axios";
import {useStore} from "vuex";

axios.interceptors.request.use((config) => {
    config.headers.set('token', useStore().getters.getToken())
    return config
}, (error) => {
    return Promise.reject(error)
})

axios.interceptors.response.use((response) => {
    return response.data
}, (error) => {
    return Promise.reject(error)
})