import React from 'react'
import ReactDOM from 'react-dom'
import Header from "./views/supply/Header"
import "./app.css"

function App() {
    return (
        <div className="">
            <Header />
        </div>
    )
}

ReactDOM.render(<App />, document.getElementById('app'))
