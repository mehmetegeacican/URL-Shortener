import { Button, IconButton, Pagination, Paper, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TableRow } from '@mui/material';
import { ITabledata } from '../interfaces/interfaces';
import DoubleArrowIcon from '@mui/icons-material/DoubleArrow';

const COLUMNS: string[] = [
    "FULL URL",
    "SHORT CODE",
    "REDIRECT"
]

const EXAMPLE_DATA:ITabledata[] = [
    {
        ID: 1,
        URL: "http://localhost",
        CODE:"B"
    },
    {
        ID: 2,
        URL: "http://localhost",
        CODE:"C"
    },
]

export default function UrlTable() {
    return (
        <TableContainer component={Paper} sx={{mt:1}}>
            <Table sx={{ minWidth: 650 ,}} aria-label="url table">
                <TableHead>
                    <TableRow>
                        <TableCell>
                            <Button variant="contained"> Add New URL </Button>
                        </TableCell>
                        <TableCell>

                        </TableCell>
                        <TableCell></TableCell>
                        <TableCell></TableCell>
                    </TableRow>
                    <TableRow>
                        {COLUMNS.map((col: string) => {
                            return (
                                <TableCell key={col}> {col}</TableCell>
                            )
                        })}
                    </TableRow>
                </TableHead>
                <TableBody >
                    {EXAMPLE_DATA.map((data:ITabledata) => {
                        return(
                            <TableRow key={data.ID}>
                                 <TableCell>{data.URL}</TableCell>
                                 <TableCell>{data.CODE}</TableCell>
                                 <TableCell>
                                    <IconButton color="primary" >
                                        <DoubleArrowIcon/>
                                    </IconButton>
                                </TableCell>
                            </TableRow> 
                        )
                    })}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TableCell>
                            <Pagination count={20} />
                        </TableCell>
                        <TableCell></TableCell>
                    </TableRow>
                </TableFooter>
            </Table>
        </TableContainer>
    )
}
