import axios from 'axios'
import api from '@/api/auth'

const login = async (kakaoCode) => {
  try {
    const response = await axios.get(`/api/auth/login?code=${kakaoCode}`)
    return response
  } catch (error) {
    console.log(error)
    throw new Error('로그인에 실패하였습니다.')
  }
}
const logout = async () => {
  try {
    const response = api.post(`/api/auth/logout`)
    return response
  } catch (error) {
    console.log(error)
    throw new Error('로그아웃에 실패하였습니다.')
  }
}

const modifyUser = async (formData) => {
  try {
    // console.log('사용자 정보 수정')
    const response = api.patch(`/api/users/info`, formData, {headers: {'Content-Type': 'multipart/form-data'}})
    return response
  } catch (error) {
    console.log(error)
    throw new Error('사용자 정보 수정에 실패하였습니다.')
  }
}

const deleteUser = async () => {
  try {
    // console.log('사용자 탈퇴')
    const response = api.delete(`/api/users`)
    return response
  } catch (error) {
    console.log(error)
    throw new Error('사용자 탈퇴에 실패하였습니다.')
  }
}

export { login, logout, modifyUser, deleteUser }
