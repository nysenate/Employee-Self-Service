import React from "react";

export default function LoginDevBanner() {
  return (
    <div className="absolute inset-0 h-24 w-full bg-yellow-600">
      <div className="text-center text-white">
        <h3 className="my-3 text-3xl">Running in dev mode</h3>
        <span className="font-medium">
          Actions performed here will not affect the production database.
        </span>
      </div>
    </div>
  );
}
