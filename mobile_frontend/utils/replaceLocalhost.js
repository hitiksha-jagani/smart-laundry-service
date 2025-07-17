export const fixImageUrl = (url) => {
  if (!url) return null;
  return url
    .replace("localhost", "192.168.29.110")
    .replace("127.0.0.1", "192.168.29.110");
};
