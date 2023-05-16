import Vue from 'vue'
import Router from 'vue-router'
import AppMain from '@/views/AppMain'
import AppStory from '@/views/AppStory'
import AppPainting from '@/views/AppPainting'
import AppBook from '@/views/AppBook'
import AppMyPage from '@/views/AppMyPage'
import AppUser from '@/views/AppUser'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Main',
      component: AppMain
    },
    {
      path: '/story',
      name: 'Story',
      component: AppStory,
      redirect: '/story/list',
      children: [
        // {
        //   path: 'keyword',
        //   name: 'StoryKeywordInput',
        //   component: () => import('@/components/story/StoryKeywordInput')
        // },
        {
          path: 'list',
          name: 'StoryList',
          component: () => import('@/components/story/StoryListView')
        },
        {
          path: 'keyword',
          name: 'StoryKeywordInput',
          component: () => import('@/components/story/StoryKeywordInputView'),
          beforeEnter: (to, from, next) => {
            const isLoggedIn = sessionStorage.getItem('isLoginUser')
            if (isLoggedIn) {
              next()
            } else {
              alert('로그인이 필요한 페이지입니다.')
              next('/story/list')
            }
          }
        },
        {
          path: 'board',
          name: 'StoryPaintingBoard',
          component: () => import('@/components/story/StoryPaintingBoardView'),
          beforeEnter: (to, from, next) => {
            const isLoggedIn = sessionStorage.getItem('isLoginUser')
            if (isLoggedIn) {
              next()
            } else {
              alert('로그인이 필요한 페이지입니다.')
              next('/story/list')
            }
            if (from.name === 'StoryResult') {
              next({ name: 'StoryKeywordInput' })
            } else if (from.name === null) {
              next({name: 'StoryList'})
            } else {
              next()
            }
          }
        },
        {
          path: 'result',
          name: 'StoryResult',
          component: () => import('@/components/story/StoryResultView'),
          beforeEnter: (to, from, next) => {
            const isLoggedIn = sessionStorage.getItem('isLoginUser')
            if (isLoggedIn) {
              next()
            } else {
              alert('로그인이 필요한 페이지입니다.')
              next('/story/list')
            }
            if (from.name === null) {
              next({name: 'StoryList'})
            }
          }
        }
      ]
    },
    {
      path: '/painting',
      name: 'Painting',
      component: AppPainting,
      redirect: '/painting/list',
      children: [
        {
          path: 'board',
          name: 'paintingboard',
          component: () => import('@/components/painting/PaintingBoardView'),
          props: true
        },
        {
          path: 'list',
          name: 'paintinglist',
          component: () => import('@/components/painting/PaintingListView')
        },
        {
          path: 'generate',
          name: 'generatesketche',
          component: () => import('@/components/painting/GenerateSketcheView')
        }
      ]
    },
    {
      path: '/book',
      name: 'Book',
      component: AppBook,
      children: [
        {
          path: 'search',
          name: 'BookSearch',
          component: () => import('@/components/book/BookSearchView')
        },
        {
          path: 'searchbypicture',
          name: 'BookSearchByPicture',
          component: () => import('@/components/book/BookSearchByPictureView')
        },
        {
          path: 'detail/:isbn',
          name: 'BookDetail',
          component: () => import('@/components/book/BookDetailView'),
          props: true
        }
      ]
    },
    {
      path: '/mypage',
      name: 'MyPage',
      redirect: '/mypage/modify',
      component: AppMyPage,
      children: [
        {
          path: 'review',
          name: 'MyReview',
          component: () => import('@/components/myPage/MyInfoReviewListView')
        },
        {
          path: 'info',
          name: 'MyInfo',
          component: () => import('@/components/myPage/MyInfo')
        },
        {
          path: 'modify',
          name: 'MyInfoModify',
          component: () => import('@/components/myPage/MyInfoModify')
        },
        {
          path: 'painting',
          name: 'MyPainting',
          component: () => import('@/components/myPage/MyPaintingListView')
        },
        {
          path: 'story',
          name: 'MyStoryListView',
          component: () => import('@/components/myPage/MyStoryListView')
        },
        {
          path: 'notification',
          name: 'MyNotification',
          component: () => import('@/components/myPage/MyNotificationBookListView')
        }
      ]
    },
    {
      path: '/user',
      name: 'User',
      component: AppUser,
      children: [
        {
          path: 'login',
          name: 'Login',
          component: () => import('@/components/user/Login')
        },
        {
          path: 'oauth',
          name: 'Oauth',
          component: () => import('@/components/user/Oauth')
        }
      ]
    }
  ]
})
