<script setup>
defineProps({
    count: {
        type: Number,
        default: 0
    },
    title: {
        type: String,
        default: ""
    },
    diff: {
        type: Number,
        default: 0
    }
})
</script>

<template>
    <h3 class="font-bold">{{ count }}</h3>
    <div class="flex justify-between">
        <span class="font-medium text-sm">{{ title }}</span>
        <span class="font-medium" :class="{ 'text-red-500' : diff > 0, 'text-blue-500': diff < 0}">
            {{ diff > 0 ? '+' + diff : diff}}
        </span>
    </div>
</template>

<style scoped>
</style>
