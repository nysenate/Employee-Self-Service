import React from "react"
import Hero from "../../../components/Hero";

const projects = [
  {name: "Pencils"},
  {name: "Paper"},
]

function Cards() {
  return (
    <ul>
      {projects.map((project) => (
        <Card key={project} description={project.name} />
      ))}
    </ul>
  )
}
function Card({description}) {
  return (
    <div className={'bg-white shadow'}>I am a card look at me {description}</div>
  );
}
export default function RequisitionFormIndex() {
  return (

    <div>
      <Hero>Requisition Form</Hero>
      <Cards/>
    </div>
  )
}
