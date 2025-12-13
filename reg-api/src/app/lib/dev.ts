"use server"

const dev = process.env.DEV

export default async function isDev() {
  return dev === "true"
}