/**
 * 统一处理图片URL
 * 后端返回的图片路径格式为 /uploads/yyyy/MM/dd/xxx.png
 * 开发环境通过 Vite proxy 代理到后端，生产环境通过 Nginx 代理
 * 因此直接返回相对路径即可，不需要硬编码 localhost:8080
 */
export function getImageUrl(url) {
  if (!url) return ''
  // 已经是完整URL（http开头）直接返回
  if (url.startsWith('http')) return url
  // 确保以 / 开头
  if (!url.startsWith('/')) url = '/' + url
  return url
}

/**
 * 从逗号分隔的图片字符串中获取第一张图片
 */
export function getFirstImage(images) {
  if (!images) return ''
  const urls = images.split(',')
  return getImageUrl(urls[0].trim())
}

/**
 * 将逗号分隔的图片字符串转为数组
 */
export function getImageList(images) {
  if (!images) return []
  return images.split(',').map(url => getImageUrl(url.trim())).filter(url => url)
}
