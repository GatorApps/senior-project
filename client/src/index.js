import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import store from './context/store';
import { ThemeProvider, createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: 'rgb(40, 87, 151)'
    },
    secondary: {
      main: 'rgb(224, 129, 46)'
    }
  },
  components: {
    MuiTooltip: {
      styleOverrides: {
        tooltip: {
          fontSize: '0.736607rem'
        }
      }
    },
    MuiTypography: {
      styleOverrides: {
        root: {
          lineHeight: 'unset'
        },
        h1: {
          'font-size': '1.875rem'
        }
      }
    },
    MuiPaper: {
      styleOverrides: {
        outlined: {
          border: '1px solid rgb(204, 204, 204)'
        },
      }
    },
    MuiButton: {
      styleOverrides: {
        contained: {
          'background-color': 'rgb(40, 87, 151)',
          '&:hover': {
            'background-color': 'rgb(17, 82, 147)'
          }
        },
        sizeMedium: {
          'font-size': '0.9375rem'
        }
      }
    }
  }
});

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Provider store={store}>
        <ThemeProvider theme={theme}>
          <Routes>
            <Route path="/*" element={<App />} />
          </Routes>
        </ThemeProvider>
      </Provider>
    </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
);