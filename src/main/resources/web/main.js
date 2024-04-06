document.getElementById('deserializeButton').addEventListener('click', function() {
    const input = document.getElementById('inputTextarea').value;
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
                document.getElementById('outputTextarea').value = data.data.result;
            } else {
                document.getElementById('outputTextarea').value = 'Error: ' + data.data.msg;
            }
        })
        .catch((error) => {
            document.getElementById('outputTextarea').value = 'An error occurred: ' + error;
        });
});