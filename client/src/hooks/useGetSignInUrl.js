import { axiosPrivate } from '../apis/backend';

const useUfGoogle = async () => {
  const searchParams = new URLSearchParams(window.location.search);
  const continueTo = searchParams.get('continueTo') || window.location.href;

  axiosPrivate.post(`/userAuth/signIn/initialize/ufgoogle?continueTo=${continueTo}`)
    .then((response) => {
      if (response?.data?.errCode === '0' && response?.data?.payload) window.location.href = response.data.payload;
    })
    .catch((error) => {
    })
}

export { useUfGoogle };