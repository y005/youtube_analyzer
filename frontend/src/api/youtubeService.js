import axios from 'axios';

export const youtubeService = {
    oauth: () => {
        return axios({
            url: '/youtube/oauth',
            method: 'get',
        })
    },
    login: (data) => {
        return axios({
            url: '/youtube/login',
            method: 'post',
            data: data
        })
    },
    findYoutubeContent: (token, params) => {
        return axios({
            url: '/youtube',
            method: 'get',
            headers: {token: token},
            params: params
        })
    }
}