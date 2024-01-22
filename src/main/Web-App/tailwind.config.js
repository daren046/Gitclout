/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',

  content: ['./pages/**/*.{html,js}', './src/app/**/*.component.html',
  './components/**/*.{html,js}'],
  theme: {
    extend: {
      'custom-color': '#1a1919',
    },
  },
  plugins: [],
}

