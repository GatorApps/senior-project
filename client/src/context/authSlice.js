import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    userInfo: null
  },
  reducers: {
    setUserInfo: (state, action) => {
      state.userInfo = action.payload;
    }
  },
});

export const { setUserInfo } = authSlice.actions;

export default authSlice.reducer;