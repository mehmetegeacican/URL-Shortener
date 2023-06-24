import { Paper, Table, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';

const COLUMNS: string[] = [
    "ID",
    "FULL URL",
    "SHORT CODE",
    "REDIRECT"
]

export default function UrlTable() {
    return (
        <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }} aria-label="url table">
                <TableHead>
                    <TableRow>
                        {COLUMNS.map((col: string) => {
                            return (
                                <TableCell> {col}</TableCell>
                            )
                        })}
                    </TableRow>
                </TableHead>
            </Table>
        </TableContainer>
    )
}
