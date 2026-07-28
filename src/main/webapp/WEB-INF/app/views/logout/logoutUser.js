export default async function logoutUser() {
  return await fetch("/logout", { method: "GET" });
}
