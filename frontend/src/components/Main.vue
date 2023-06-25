<script setup>
import {ref} from "vue";

const youtubeInfos = ref([{ channelName: "chxelin", title: "[𝒫𝓁𝒶𝓎𝓁𝒾𝓈𝓉 ] ✿ 들을 수 있는 플레이리스트 ✿", viewCount: 153213, likeCount: 14321, sentimentRatio: 61, relatedKeyword: ["여자 아이돌","afd","adfadf","afdafdsa"]}])
const commentInfos = ref([{ userId: "@yj2620", content: "노래 취향저격이에요 너무 좋음 ❤", likeCount: 1}])

function updateComments() {
    commentInfos.value = []
}
</script>

<template>
    <div class="mx-auto max-w-screen-2xl p-4 md:p-6 2xl:p-10">
        <div class="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-6 xl:grid-cols-2 2xl:gap-7.5">
            <!-- Card Item Start -->
            <div class="rounded-lg border border-stroke bg-white py-3 px-7.5 shadow-default dark:border-strokedark dark:bg-boxdark">
                <div class="flex h-11.5 w-11.5 items-center justify-center rounded-full bg-meta-2 dark:bg-meta-4">
                </div>

                <div class="flex items-end justify-between">
                    <div class="p-4">
                        <h3 class="text-title-md font-bold text-gray-700 dark:text-white">
                            13
                        </h3>
                        <span class="text-gray-700 text-sm font-medium">구독 중인 채널 수</span>
                    </div>
                    <span class="pr-8 flex items-center gap-1 text-sm text-red-500 font-medium text-meta-3">
                    +1
                    </span>
                </div>
            </div>
            <!-- Card Item End -->

            <!-- Card Item Start -->
            <div class="rounded-lg border border-stroke bg-white py-3 px-7.5 shadow-default dark:border-strokedark dark:bg-boxdark">
                <div class="flex h-11.5 w-11.5 items-center justify-center rounded-full bg-meta-2 dark:bg-meta-4">
                </div>

                <div class="flex items-end justify-between">
                    <div class="p-4">
                        <h3 class="text-title-md font-bold text-gray-700 dark:text-white">
                            10
                        </h3>
                        <span class="text-gray-700 text-sm font-medium">오늘 새로 업로드 된 영상 수</span>
                    </div>
                    <span class="pr-8 flex items-center gap-1 text-sm text-red-500 font-medium text-meta-3">
                    +10
                    </span>
                </div>
            </div>
            <!-- Card Item End -->
        </div>

        <div class="mt-4 grid grid-cols-12 gap-4 md:mt-6 md:gap-6 2xl:mt-7.5 2xl:gap-7.5">
            <!-- ====== Table One Start -->
            <div class="col-span-12 xl:col-span-12">
                <div class="video-container rounded-lg border border-stroke bg-white p-5 shadow-default dark:border-strokedark dark:bg-boxdark sm:p-7.5 xl:p-1">
                    <iframe src="https://www.youtube.com/embed/ibYD23-dtjo" title="YouTube Player" frameborder="0" allow="accelerometer; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                </div>
            </div>
            <!-- ====== Table One End -->

            <!-- ====== Table One Start -->
            <div class="col-span-12 xl:col-span-8">
                <div class="rounded-lg border border-stroke bg-white p-5 shadow-default dark:border-strokedark dark:bg-boxdark sm:px-7.5 xl:pb-1">
                    <h4 class="mb-5 text-xl font-bold text-gray-700 dark:text-white">
                        채널 영상 분석
                    </h4>

                    <div class="flex flex-col">
                        <div class="text-center grid grid-cols-3 border-b border-stroke bg-gray-2 dark:bg-meta-4 sm:grid-cols-6">
                            <div class="p-2.5 xl:p-5">
                                <h5 class="text-gray-700 text-sm font-medium uppercase xsm:text-base">
                                    채널명
                                </h5>
                            </div>
                            <div class="p-2.5 xl:p-5">
                                <h5 class="text-gray-700 text-sm font-medium uppercase xsm:text-base">
                                    제목
                                </h5>
                            </div>
                            <div class="p-2.5 xl:p-5">
                                <h5 class="text-gray-700 text-sm font-medium uppercase xsm:text-base">
                                    조회수
                                </h5>
                            </div>
                            <div class="p-2.5 xl:p-5">
                                <h5 class="text-gray-700 text-sm font-medium uppercase xsm:text-base">
                                    좋아요 수
                                </h5>
                            </div>
                            <div class="p-2.5 xl:p-5">
                                <h5 class="text-gray-700 text-sm font-medium uppercase xsm:text-base">
                                    댓글 긍정 비율
                                </h5>
                            </div>
                            <div class="p-2.5 xl:p-5">
                                <h5 class="text-gray-700 text-sm font-medium uppercase xsm:text-base">
                                    연관 키워드
                                </h5>
                            </div>
                        </div>
<!--                        마지막 데이터는 border-b border-stroke 클래스 제외-->
                        <div @click="updateComments" v-for="youtubeInfo in youtubeInfos" class="grid grid-cols-3 dark:border-strokedark cursor-pointer hover:bg-gray-100 hover:rounded-lg sm:grid-cols-6">
                            <div class="flex items-center justify-center p-2.5 xl:p-5">
                                <p class="text-black dark:text-white">
                                    {{ youtubeInfo.channelName }}
                                </p>
                            </div>

                            <div class="flex items-center justify-center p-2.5 xl:p-5">
                                <p class="text-black dark:text-white">
                                    {{ youtubeInfo.title }}
                                </p>
                            </div>

                            <div class="flex items-center justify-center p-2.5 xl:p-5">
                                <p class="text-black dark:text-white">
                                    {{ youtubeInfo.viewCount }}
                                </p>
                            </div>

                            <div class="flex items-center justify-center p-2.5 xl:p-5">
                                <p class="text-black dark:text-white">
                                    {{ youtubeInfo.likeCount }}
                                </p>
                            </div>

                            <div class="flex items-center justify-center p-2.5 xl:p-5">
                                <p class="text-black dark:text-white">
                                    {{ youtubeInfo.sentimentRatio + "%" }}
                                </p>
                            </div>

                            <div class="flex items-center justify-center p-2.5 xl:p-5">
                                <span v-for="keyword in youtubeInfo.relatedKeyword" class="bg-gray-400 text-white px-1 mx-0.5 rounded-lg dark:text-white">
                                    {{ keyword }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <!-- ====== Table One End -->

            <!-- ====== Chat Card Start -->
            <div class="col-span-12 xl:col-span-4 rounded-lg border border-stroke bg-white p-5 shadow-default dark:border-strokedark dark:bg-boxdark">
                <h4 class="mb-5 px-7.5 text-xl font-bold text-gray-700 dark:text-white">
                    인기 댓글
                </h4>
                <div class="border border-stroke"></div>
                <div>
                    <div v-if="commentInfos.length === 0" class="flex items-center gap-5 py-3 px-7.5 hover:bg-gray-3 dark:hover:bg-meta-4">
                        <div class="flex flex-1 items-center justify-between">
                            <div>
                                <h5 class="font-medium text-gray-700 dark:text-white">
                                    댓글이 없습니다.
                                </h5>
                            </div>
                        </div>
                    </div>
                    <div v-else v-for="comment in commentInfos" class="flex items-center gap-5 py-3 px-7.5 hover:bg-gray-3 dark:hover:bg-meta-4">
                        <div class="flex flex-1 items-center justify-between">
                            <div>
                                <h5 class="font-medium text-black dark:text-white">
                                    {{ comment.userId }}
                                </h5>
                                <p>
                                    <span class="text-sm font-medium text-black dark:text-white">{{ comment.content }}</span>
                                </p>
                            </div>
                            <div class="flex h-6 w-6 items-center justify-center rounded-full bg-primary">
                                <span class="text-sm font-medium text-black">{{ comment.likeCount }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- ====== Chat Card End -->
        </div>
    </div>
</template>

<style scoped>
.video-container {
    position: relative;
    height: 0;
    padding-bottom: 56.25%;
}

.video-container iframe {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}
</style>