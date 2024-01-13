<script setup>
import {ref} from "vue";

const youtubeInfos = ref([{ channelName: "chxelin", title: "[𝒫𝓁𝒶𝓎𝓁𝒾𝓈𝓉 ] ✿ 들을 수 있는 플레이리스트 ✿", viewCount: 153213, likeCount: 14321, sentimentRatio: 61, relatedKeyword: ["여자 아이돌","afd","adfadf","afdafdsa"]}])
const commentInfos = ref([{ userId: "@yj2620", content: "노래 취향저격이에요 너무 좋음 ❤", likeCount: 1}])

function updateComments() {
    commentInfos.value = []
}
</script>

<template>
    <div class="flex justify-around">
        <div class="w-full bg-white border rounded-lg">
            로그인 화면
        </div>
    </div>
    <div class="flex justify-around text-sm text-gray-700">
        <div class="w-1/2 bg-white border rounded-lg">
            <h3 class="font-bold">13</h3>
            <div class="flex justify-between">
                <span class="font-medium">구독 중인 채널 수</span>
                <span class="text-red-500 font-medium">+1</span>
            </div>
        </div>
        <div class="w-1/2 bg-white border rounded-lg">
            <h3 class="font-bold">10</h3>
            <div class="flex justify-between">
                <span class="font-medium">오늘 새로 업로드 된 영상 수</span>
                <span class="text-red-500 font-medium">+10</span>
            </div>
        </div>
    </div>
    <div class="flex justify-center rounded-lg border p-5">
        <iframe
            class="w-3/4 h-96"
            src="https://www.youtube.com/embed/ibYD23-dtjo"
            title="YouTube Player"
            allow="accelerometer; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen/>
    </div>
    <div class="flex justify-around">
        <div class="w-2/3 bg-white border rounded-lg">
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
                class="flex justify-around text-gray-700 text-sm font-medium">
                <div>{{ youtubeInfo.channelName }}</div>
                <div>{{ youtubeInfo.title }}</div>
                <div>{{ youtubeInfo.viewCount }}</div>
                <div>{{ youtubeInfo.likeCount }}</div>
                <div>{{ youtubeInfo.sentimentRatio + "%" }}</div>
                <div>
                    <span
                        v-for="keyword in youtubeInfo.relatedKeyword"
                        class="bg-gray-400 text-white px-1 mx-0.5 rounded-lg">
                        {{ keyword }}
                    </span>
                </div>
            </div>
        </div>
        <div class="w-1/3 bg-white border rounded-lg">
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
</template>

<style scoped></style>