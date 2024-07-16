const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const Dotenv = require('dotenv-webpack');

module.exports = {
  entry: './WEB-INF/app/index.js',
  output: {
    path: path.resolve(__dirname, 'assets/dist'),
    filename: 'index_bundle.js',
    publicPath: process.env.NODE_ENV === 'production' ? '/assets/dist/' : '/'
  },
  resolve: {
    alias: {
      app: path.resolve(__dirname, 'WEB-INF/app')
    }
  },
  module: {
    rules: [
      // Load CSS files, including TailwindCSS
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader", "postcss-loader"],
      },
      // Transpile JS
      {
        test: /\.(js)$/,
        use: 'babel-loader'
      },
      // Load image files
      {
        test: /\.(png|jpe?g|gif|svg)$/i,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[path][name].[ext]',
            },
          },
        ],
      },
    ]
  },
  plugins: [
    new HtmlWebpackPlugin(
      {
        template: 'WEB-INF/app/index.html'
      }),
    new Dotenv(
      {
        path: 'react-properties.env'
      }),
  ],
  mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
  devServer: {
    port: 3000,
    // Send API requests for these paths to the target base URL while in dev mode.
    proxy: [
      {
        context: ['/api', '/assets'],
        target: 'http://localhost:8080',
        secure: false,
        changeOrigin: true,
      },
      // For the /login page, only proxy POST requests.
      {
        context: ['/login'],
        target: 'http://localhost:8080',
        secure: false,
        changeOrigin: true,
        bypass: req => req.method !== 'POST' ? '/login' : undefined
      }
    ],
    historyApiFallback: {
      disableDotRule: true,
    },
    static: ['../assets']
  },
  devtool: process.env.NODE_ENV === 'production' ? false : 'eval-source-map'
};
