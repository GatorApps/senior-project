import { createSlice } from '@reduxjs/toolkit';

const alertSlice = createSlice({
  name: 'alert',
  initialState: {
    appAlert: null
  },
  reducers: {
    setAppAlert: (state, action) => {
      state.appAlert = action.payload;
    }
  },
});

export const { setAppAlert } = alertSlice.actions;

export default alertSlice.reducer;