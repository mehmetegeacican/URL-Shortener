import { IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TableRow } from '@mui/material';
import { ITabledata } from '../interfaces/interfaces';
import DoubleArrowIcon from '@mui/icons-material/DoubleArrow';

const COLUMNS: string[] = [
    "ID",
    "FULL URL",
    "SHORT CODE",
    "REDIRECT"
]

const EXAMPLE_DATA:ITabledata[] = [
    {
        ID: 1,
        URL: "http://localhost",
        CODE:"B",
        REDIRECT:null
    },
    {
        ID: 2,
        URL: "http://localhost",
        CODE:"C",
        REDIRECT:null
    },
]

export default function UrlTable() {
    return (
        <TableContainer component={Paper} sx={{mt:1, alignItems:"center"}}>
            <Table sx={{ minWidth: 750 ,}} aria-label="url table">
                <TableHead>
                    <TableRow>
                        {COLUMNS.map((col: string) => {
                            return (
                                <TableCell> {col}</TableCell>
                            )
                        })}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {EXAMPLE_DATA.map((data:ITabledata) => {
                        return(
                            <TableRow key={data.ID}>
                                 <TableCell>{data.ID}</TableCell>
                                 <TableCell>{data.URL}</TableCell>
                                 <TableCell>{data.CODE}</TableCell>
                                 <TableCell>
                                    <IconButton color="primary" >
                                        <DoubleArrowIcon/>
                                    </IconButton></TableCell>
                            </TableRow>
                           
                        )
                    })}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TableCell>
                            Pagination
                        </TableCell>
                    </TableRow>
                </TableFooter>
            </Table>
        </TableContainer>
    )
}
