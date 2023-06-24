import { AppBar, Button, Toolbar, Typography } from '@mui/material'

export default function Navbar() {
    return (
        <AppBar position="fixed" sx={{ width: "100%" }}>
            <Toolbar sx={{justifyContent:'space-between'}}>
                
                <Typography variant="h6" color="inherit" noWrap>
                    Url list
                </Typography>
                <Button color="inherit">Login</Button>
            </Toolbar>
        </AppBar>
    )
}
