import { AppBar, Box, Toolbar, Typography } from '@mui/material'

export default function Footer() {
    return (
        <AppBar position="fixed" sx={{ top: 'auto', bottom: 0,height:40, width: '100%',backgroundColor: 'transparent', boxShadow: '0px -2px 10px rgba(0, 0, 0, 0.07)' }}>
            <Toolbar>
                <Box sx={{ margin: 'auto' }}>
                    <Typography variant="body1" color="textSecondary" sx={{textAlign:"center"}}>
                        &copy; Url Shortener. Content for footer here
                    </Typography>
                </Box>
            </Toolbar>
        </AppBar>
    )
}
