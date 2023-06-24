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
        <Navbar />
        <main>
          <Box sx={{ display: 'flex', flexDirection: 'column'}}>
            <Stack spacing={2}>
              <UrlTable />
            </Stack>
          </Box>
        </main>
        <Footer />
      </ThemeProvider>
    </div>
  )
}

export default App
