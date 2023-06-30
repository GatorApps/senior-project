import { Fragment, useState, useEffect } from "react";
import { Box, Button, Container, Dialog, DialogActions, DialogTitle, DialogContent, DialogContentText, Divider, FormControl, Grid, Grow, InputAdornment, InputLabel, OutlinedInput, Paper, TextField, Tooltip, Typography } from '@mui/material';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';

const GenericForm = ({ key, data, setData, schema, fullWidth }) => {
  const handleAttributeOnChange = (attribute, newValue) => {
    setData(prevData => ({
      ...prevData,
      [attribute]: newValue
    }));
  };

  return (
    <Grid container spacing={3}>
      {Object.keys(schema).map((attribute) => {
        const attributeSchema = schema[attribute];

        return (
          <Grid item xs={12} sm={12} md={fullWidth ? 12 : 6}>
            <FormControl required={attributeSchema.required} disabled={!attributeSchema.mutable} fullWidth variant="outlined">
              {(attributeSchema.inputType === 'string_singleline') ? (
                <Fragment>
                  <InputLabel htmlFor={`form-${key}-${attribute}`}>{attributeSchema.label}</InputLabel>
                  <OutlinedInput
                    id={`form-${key}-${attribute}`}
                    value={data && data[attribute]}
                    onChange={(e) => handleAttributeOnChange(attribute, e.target.value)}
                    label={attributeSchema.label}
                  />
                </Fragment>
              ) : (attributeSchema.inputType === 'string_multiline') ? (
                <Fragment>
                  <InputLabel htmlFor={`form-${key}-${attribute}`}>{attributeSchema.label}</InputLabel>
                  <OutlinedInput
                    id={`form-${key}-${attribute}`}
                    value={data && data[attribute]}
                    onChange={(e) => handleAttributeOnChange(attribute, e.target.value)}
                    label={attributeSchema.label}
                    multiline
                    minRows={2}
                  />
                </Fragment>
              ) : (attributeSchema.inputType === 'date_month') ? (
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <DatePicker
                    id={`form-${key}-${attribute}`}
                    value={data && (data[attribute] ? dayjs(data[attribute]) : undefined)}
                    onChange={(value) => { try { handleAttributeOnChange(attribute, value?.toISOString()) } catch (err) { } }}
                    label={attributeSchema.label}
                    views={['year', 'month']}
                  />
                </LocalizationProvider>
              ) : (
                <Fragment>
                  <InputLabel htmlFor={`form-${key}-${attribute}`}>{attributeSchema.label}</InputLabel>
                  <OutlinedInput
                    id={`form-${key}-${attribute}`}
                    value={data && data[attribute]}
                    onChange={(e) => handleAttributeOnChange(attribute, e.target.value)}
                    label={attributeSchema.label}
                  />
                </Fragment>
              )
              }
            </FormControl>
          </Grid>
        );
      })}
    </Grid >
  );
}

export default GenericForm;