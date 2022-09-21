module.exports = {
  mode: 'jit',
  purge: [
    './WEB-INF/app/**/*.js',
    './WEB-INF/app/**/*.html',
  ],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {
      colors: {
        teal: {
          100: '#EDF4F6',
          200: '#DBEAED',
          300: '#BCE4EC',
          400: '#B8E3EB',
          500: '#4195A7',
          600: '#016B80',
          700: '#016073',
          800: '#005667',
          900: '#004552',
        },
        green: {
          100: '#F1F5EC',
          200: '#E5EADB',
          300: '#c9d6ad',
          400: '#afc285',
          500: '#94ad5c',
          600: '#799933',
          700: '#5B7325',
          800: '#556b24',
          900: '#495c1f',
        },
        purple: {
          100: '#eff1f8',
          200: '#e0e2f2',
          300: '#c1c6e5',
          400: '#929bd1',
          500: '#737ec4',
          600: '#6370BD',
          700: '#5060B5',
          800: '#404d91',
          900: '#303a6d',
        },
        orange: {
          100: '#FAF4EB',
          200: '#F5EAD9',
          300: '#edd5a8',
          400: '#e3bf7d',
          500: '#daaa51',
          600: '#D19526',
          700: '#BB8620',
          800: '#966b1a',
          900: '#705013',
        },
        gray: {
          50: '#FAFAFA',
          100: '#F1F1F1',
          450: '#848B98',
        }
      },
      borderWidth: {
        '1': '1px'
      },
      fontSize: {
        'xs': '0.688rem',
        'sm': '0.75rem',
        'base': '0.813rem',
        'lg': '1rem',
        'xl': '1.125rem',
        '2xl': '1.25rem',
        '3xl': '1.5rem',
        '4xl': '1.875rem',
        '5xl': '2.25rem',
        '6xl': '3rem',
      },
      fontFamily: {
        openSans: [ 'Open Sans', 'sans-serif' ],
      },
    },
  },
  variants: {
    extend: {
      borderWidth: [ 'responsive', 'hover', 'focus' ],
      translate: [ 'responsive', 'hover', 'focus', 'active' ],
      boxShadow: [ 'disabled' ],
      cursor: [ 'disabled' ],
      backgroundColor: [ 'disabled' ],
      opacity: [ 'disabled' ]
    },
  },
  plugins: [],
}
