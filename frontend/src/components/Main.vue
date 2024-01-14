<script setup>
import {onMounted, ref, toRaw} from "vue";
import {youtubeService} from "../api/youtubeService.js";

const loginForm = ref({userId: "", password: ""})
const youtubeId = ref("")
const youtubeInfos = ref([{ channelName: "chxelin", title: "[𝒫𝓁𝒶𝓎𝓁𝒾𝓈𝓉 ] ✿ 들을 수 있는 플레이리스트 ✿", viewCount: 153213, likeCount: 14321, sentimentRatio: 61, relatedKeyword: ["여자 아이돌","afd","adfadf","afdafdsa"]}])
const commentInfos = ref([{ userId: "@yj2620", content: "노래 취향저격이에요 너무 좋음 ❤", likeCount: 1}])
const keyword = ref()
function updateComments() {
    commentInfos.value = []
}

function clickVideo(videoId) {
    youtubeId.value = videoId
}

function search() {

}

async function oauth() {
    try {
        const response = await youtubeService.oauth(toRaw(loginForm.value))
    } catch (error) {

    }
}

async function login() {
    if (!loginForm.value.userId || !loginForm.value.password) {
        alert("아이디와 비밀번호를 모두 입력하세요.")
    }
    try {
        const response = await youtubeService.login(toRaw(loginForm.value))
    } catch (error) {

    }
}
async function getYoutubeContent() {
    const response = await youtubeService.findYoutubeContent()
    youtubeInfos.value = response
}

onMounted(() => {

})
</script>

<template>
    <div class="bg-white border rounded-lg shadow flex justify-between p-3 items-center">
        <div class="p-1 border rounded-lg bg-red-600">
            <a href="#" class="text-white font-bold">Youtube Analyzer</a>
        </div>
        <div class=" w-96">
            <div class="flex">
                <input
                    class="w-full border border-2 mr-3"
                    type="text"
                    v-model.trim="keyword"
                    placeholder=" search video">
                <button
                    class="border rounded-lg px-0.5 py-1 hover:bg-gray-100"
                    type="button"
                    @click="search">
                    Search
                </button>
            </div>
        </div>
        <div class="flex justify-end items-center">
            <div>
                ID:&af;
                <input
                    class="mr-3 border border-2"
                    type="text"
                    v-model.trim="loginForm.userId">
            </div>
            <div>
                PASSWD:&af;
                <input
                    class="mr-3 border border-2"
                    type="password"
                    v-model.trim="loginForm.password">
            </div>
            <button
                class="border rounded-lg px-2 py-1 mr-1 hover:bg-gray-100"
                type="button"
                @click="login">
                Login
            </button>
            <button
                class="border rounded-lg px-0.5 py-1 hover:bg-gray-100"
                type="button"
                @click="oauth">
                Youtube Oauth
            </button>
        </div>
    </div>
    <div class="h-screen">
        <div class="flex justify-around text-sm text-gray-700">
            <div class="w-1/2 bg-white border rounded-lg m-1 shadow p-5">
                <h3 class="font-bold">13</h3>
                <div class="flex justify-between">
                    <span class="font-medium">구독 중인 채널 수</span>
                    <span class="text-red-500 font-medium">+1</span>
                </div>
            </div>
            <div class="w-1/2 bg-white border rounded-lg m-1 shadow p-5">
                <h3 class="font-bold">10</h3>
                <div class="flex justify-between">
                    <span class="font-medium">오늘 새로 업로드 된 영상 수</span>
                    <span class="text-red-500 font-medium">+10</span>
                </div>
            </div>
        </div>
        <div class="flex justify-center rounded-lg border p-5 shadow">
            <iframe
                v-if="youtubeId.length > 0"
                class="w-3/4 h-3/4"
                :src="'https://www.youtube.com/embed/' + youtubeId"
                title="YouTube Player"
                allow="accelerometer; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen/>
            <div v-else
                class="w-3/4 h-3/4 flex justify-center">
                채널 영상을 클릭하세요
            </div>
        </div>
        <div class="flex justify-around">
            <div class="w-2/3 bg-white border rounded-lg shadow">
                <h4 class="ml-2 text-xl font-bold text-gray-700">채널 영상 분석</h4>
                <hr>
                <div class="flex justify-around text-gray-700 text-sm font-medium">
                    <div>채널명</div>
                    <div>제목</div>
                    <div>조회수</div>
                    <div>좋아요 수</div>
                    <div>댓글 긍정 비율</div>
                    <div>연관 키워드</div>
                </div>
                <div
                    v-for="youtubeInfo in youtubeInfos"
                    @click="clickVideo(youtubeInfo.id)"
                    class="flex justify-around text-gray-700 text-sm font-medium">
                    <div>{{ youtubeInfo.channelName }}</div>
                    <div>{{ youtubeInfo.title }}</div>
                    <div>{{ youtubeInfo.viewCount }}</div>
                    <div>{{ youtubeInfo.likeCount }}</div>
                    <div>{{ youtubeInfo.sentimentRatio + "%" }}</div>
                    <div>
                    <span
                        @click="search(keyword)"
                        v-for="keyword in youtubeInfo.relatedKeyword"
                        class="bg-gray-400 text-white px-1 m-0.5 rounded">
                        {{ keyword }}
                    </span>
                    </div>
                </div>
            </div>
            <div class="w-1/3 bg-white border rounded-lg shadow">
                <h4 class="ml-2 text-xl font-bold text-gray-700">인기 댓글</h4>
                <hr>
                <div v-if="commentInfos.length === 0">
                    <h5 class="font-medium text-gray-700">
                        댓글이 없습니다.
                    </h5>
                </div>
                <div
                    v-else
                    v-for="comment in commentInfos"
                    class="flex justify-around text-gray-700 text-sm font-medium">
                    <div>{{ comment.userId }}</div>
                    <div>{{ comment.content }}</div>
                    <div>{{ comment.likeCount }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>