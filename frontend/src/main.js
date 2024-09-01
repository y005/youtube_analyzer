import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import {userStore} from "./store/userStore.js";

createApp(App).use(userStore).mount('#app')