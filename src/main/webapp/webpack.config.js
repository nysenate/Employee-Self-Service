const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const isProduction = process.env.NODE_ENV === "production";
const essIp = process.env.ESS_IP;

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
    // Set ESS_IP to work against a Tomcat on another loopback address, e.g. one per
    // worktree: `ESS_IP=127.0.1.3 npm run dev` serves http://127.0.1.3:3000 and sends
    // API requests to 127.0.1.3:8080. Without it, API requests go to localhost:8080.
    ...(essIp && { host: essIp }),
    port: 3000,
    // Send API requests for these paths to the target base URL while in dev mode.
    proxy: [
      {
        context: ["/api", "/assets", "/logout"],
        target: `http://${essIp || "localhost"}:8080`,
        secure: false,
        changeOrigin: true,
      },
      // For the /login page, only proxy POST requests.
      {
        context: ["/login"],
        target: `http://${essIp || "localhost"}:8080`,
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
