const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const isProduction = process.env.NODE_ENV === "production";

module.exports = {
  entry: {
    main: "./WEB-INF/app/index.js",
  },
  output: {
    path: path.resolve(__dirname, "assets/dist"),
    filename: isProduction ? "[name].[contenthash].js" : "[name].bundle.js",
    chunkFilename: isProduction
      ? "[name].[contenthash].js"
      : "[name].bundle.js",
    publicPath: isProduction ? "/assets/dist/" : "/",
    clean: true,
  },
  resolve: {
    extensions: [".js", ".jsx", ".json"],
    alias: {
      app: path.resolve(__dirname, "WEB-INF/app"),
    },
  },
  module: {
    rules: [
      // Load CSS files, including TailwindCSS
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader", "postcss-loader"],
      },
      // Transpile JS and JSX
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: "babel-loader",
      },
    ],
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: "WEB-INF/app/index.html",
    }),
  ],
  context: __dirname,
  mode: isProduction ? "production" : "development",
  devServer: {
    port: 3000,
    // Send API requests for these paths to the target base URL while in dev mode.
    proxy: [
      {
        context: ["/api", "/assets", "/logout"],
        target: "http://localhost:8080",
        secure: false,
        changeOrigin: true,
      },
      // For the /login page, only proxy POST requests.
      {
        context: ["/login"],
        target: "http://localhost:8080",
        secure: false,
        changeOrigin: true,
        bypass: (req) => (req.method !== "POST" ? "/login" : undefined),
      },
    ],
    historyApiFallback: {
      disableDotRule: true,
    },
    static: ["../assets"],
  },
  devtool: isProduction ? false : "eval-source-map",
};
