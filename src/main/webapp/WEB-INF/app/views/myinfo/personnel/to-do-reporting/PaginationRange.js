import * as React from 'react';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import { styled } from "@mui/material";

// Custom Pagination component with styled-components
const CustomPagination = styled(Pagination)(({ theme }) => ({
  '& .MuiPaginationItem-root': {
    borderRadius: 0, // Remove border radius
    padding: theme.spacing(0.5), // Adjust padding as needed
    margin: theme.spacing(0.3), // Adjust margin as needed
    color: theme.palette.text.secondary, // Text color
    '&:hover': {
      backgroundColor: theme.palette.action.hover, // Background color on hover
    },
  },
  '& .Mui-selected': {
    backgroundColor: theme.palette.common.white, // Background color for selected item
    color: theme.palette.text.primary, // Text color for selected item
    '&:hover': {
      backgroundColor: theme.palette.common.white, // Darker background color on hover
    },
  },
  '& .MuiPaginationItem-icon': {
    borderRadius: 'initial', // Ensure icons are not rounded
  },
}));

export default function PaginationRange() {
  return (
    <Stack className={"mr-10 border-1 font-inherit text-sm text-gray-200"} spacing={5}>
      <CustomPagination
        count={110}
        color="primary"
        defaultPage={1}
        siblingCount={2}
        boundaryCount={1}
        size="sm"
        showFirstButton
        showLastButton />
    </Stack>
  );
}
