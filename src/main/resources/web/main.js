document.getElementById('deserializeButton').addEventListener('click', function() {
    const input = document.getElementById('miniMessageTextarea').value;
    fetch('/deserialize', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({message: input})
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('textComponentTextarea').value = data.data.result;
            } else {
                // Display error using SweetAlert in dark mode
                Swal.fire({
                    title: 'Error!',
                    text: data.data.msg,
                    icon: 'error',
                    confirmButtonText: 'Close',
                    theme: 'dark'
                });
            }
        })
        .catch((error) => {
            // Display error using SweetAlert in dark mode
            Swal.fire({
                title: 'An error occurred',
                text: error.toString(),
                icon: 'error',
                confirmButtonText: 'Close',
                theme: 'dark'
            });
        });
});

document.getElementById('serializeButton').addEventListener('click', function() {
    const input = document.getElementById('textComponentTextarea').value;
    fetch('/serialize', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({message: input})
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('miniMessageTextarea').value = data.data.result;
            } else {
                // Display error using SweetAlert in dark mode
                Swal.fire({
                    title: 'Error!',
                    text: data.data.msg,
                    icon: 'error',
                    confirmButtonText: 'Close',
                    theme: 'dark'
                });
            }
        })
        .catch((error) => {
            // Display error using SweetAlert in dark mode
            Swal.fire({
                title: 'An error occurred',
                text: error.toString(),
                icon: 'error',
                confirmButtonText: 'Close',
                theme: 'dark'
            });
        });
});
