import { Paper, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TableRow } from '@mui/material';

const COLUMNS: string[] = [
    "ID",
    "FULL URL",
    "SHORT CODE",
    "REDIRECT"
]

const EXAMPLE_DATA:number[] = [1,2,3,4,5,6,7]

export default function UrlTable() {
    return (
        <TableContainer component={Paper} sx={{mt:1}}>
            <Table sx={{ minWidth: 750, px : 50 }} aria-label="url table">
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
                    {EXAMPLE_DATA.map((data:number) => {
                        return(
                            <TableRow>
                                 <TableCell>{data}</TableCell>
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
