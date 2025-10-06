import React from "react";


export const themes = {
  myinfo: "myinfo",
  time: "time",
  supply: "supply",
  travel: "travel",
}

export const ThemeContext = React.createContext(themes.time);
