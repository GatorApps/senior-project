import { Alert as MUIAlert, AlertTitle, Box, Button, Typography } from '@mui/material';
import './Alert.css'

const Alert = ({ data, style }) => {
  return (
    <Box className='Alert'>
      <MUIAlert severity={data?.severity || "error"} action={
        <Box sx={{ marginRight: '12px' }}>
          {data?.actions?.map((action) => {
            return <Button color="inherit" variant="outlined" size="small" onClick={action.onClick} sx={{ margin: '6px' }}>{action.name}</Button>
          })}
        </Box>
      } sx={{ padding: '16px' }}>
        <AlertTitle sx={{ fontSize: style?.titleFontSize || '18px' }}>{data?.title || "Error"}</AlertTitle>
        <Typography sx={{ fontSize: style?.textFontSize || '16px' }}>{data?.message || "Unknown error"}</Typography>
      </MUIAlert>
    </Box>
  )
};

export default Alert;