
import { AppBar, Box, Button, Container, CssBaseline, IconButton, Paper, Stack, Toolbar, Typography } from "@mui/material"
import UrlTable from "./components/UrlTable"
import { createTheme, ThemeProvider } from '@mui/material/styles';
import Navbar from "./components/layout/Navbar";
import Footer from "./components/layout/Footer";

function App() {
  const defaultTheme = createTheme();

  return (
    <div>
      <ThemeProvider theme={defaultTheme}>
        <CssBaseline />
        <Box sx={{ mt:  2,  display: 'flex', justifyContent: 'center' }}>
            <Stack spacing={2} alignItems={"center"}>
              <Container maxWidth= "md" sx={{alignItems:"center"}}>
                <UrlTable />
              </Container>
            </Stack>
          </Box>
        <Navbar />
        <main>
          
        </main>
        <Footer />
      </ThemeProvider>
    </div>
  )
}

export default App
