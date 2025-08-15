import { fetchJson } from "app/api/fetchJson";

export default async function loginUser(username, password) {
  const body = new URLSearchParams();
  body.append("username", username);
  body.append("password", password);

  let data;
  try {
    data = await fetchJson(`/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: body,
    });
  } catch (error) {
    console.error(error);
  }

  if (data.authenticated) {
    // successfully logged in.
    return data;
  } else {
    // Unsuccessful login.
    throw new Error(data?.message);
  }
}
