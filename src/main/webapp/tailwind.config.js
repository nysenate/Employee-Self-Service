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
                    300: '#D7EFF4',
                    400: '#BCE4EC',
                    500: '#B8E3EB',
                    600: '#4195A7',
                    700: '#016B80',
                    800: '#016073',
                    900: '#005667',
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
        },
    },
    variants: {
        extend: {
            borderWidth: ['responsive', 'hover', 'focus'],
            translate: ['responsive', 'hover', 'focus', 'active'],
            boxShadow: ['disabled'],
            cursor: ['disabled'],
            backgroundColor: ['disabled'],
            opacity: ['disabled']
        },
    },
    plugins: [],
}
