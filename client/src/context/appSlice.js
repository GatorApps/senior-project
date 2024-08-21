import { createSlice } from '@reduxjs/toolkit';

const appSlice = createSlice({
  name: 'app',
  initialState: {
    appInfo: null
  },
  reducers: {
    setAppInfo: (state, action) => {
      state.appInfo = action.payload;
    }
  },
});

export const { setAppInfo } = appSlice.actions;

export default appSlice.reducer;