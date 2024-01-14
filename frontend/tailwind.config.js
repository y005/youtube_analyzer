/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [
    (({ addUtilities }) => {
      addUtilities({
        '.container': {
          '@apply bg-white border rounded-lg shadow m-1 p-5': '',
        },
        '.input': {
          '@apply border border-2 rounded-lg': {}
        }
      })
    })
  ],
}