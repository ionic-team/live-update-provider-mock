const webpack = require("webpack");

const remoteName = process.env.MF_REMOTE_NAME || "helpinfo";

module.exports = {
  webpack: {
    configure: (config) => {
      config.output.publicPath = "auto";
      config.optimization.runtimeChunk = false;

      config.plugins.push(
        new webpack.container.ModuleFederationPlugin({
          name: remoteName,
          filename: "remoteEntry.js",
          exposes: {
            "./HelpDetails": "./src/HelpDetails",
            "./App": "./src/App"
          },
          shared: {
            react: { singleton: true, requiredVersion: false },
            "react-dom": { singleton: true, requiredVersion: false }
          }
        })
      );

      return config;
    }
  },
  devServer: {
    port: 4301,
    headers: {
      "Access-Control-Allow-Origin": "*"
    }
  }
};
