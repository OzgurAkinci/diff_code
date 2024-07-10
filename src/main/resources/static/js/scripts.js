$(document).ready(function() {
    $(".spinner").hide();
    $(".results").hide();
    $('.errorDiv').hide();
    $("#btn").click(function(event) {
        event.preventDefault();
        $(".spinner").show();
        let v = document.getElementById("v");
        let mTxt = document.getElementById("mTxt");
        let hVal = document.getElementById("hVal");
        //let yVal = document.getElementById("yVal");
        let yVals = document.getElementById("yVals");
        let toTextValue = document.getElementById("toTextValue");
        let toPdfValue = document.getElementById("toPdfValue");
        let formDataV = {
            'd': v.value,
            'm': mTxt.value,
            'h': hVal.value,
            //'y': yVal.value,
            'yvals': yVals.value,
            'toText': toTextValue.checked,
            'toPdf': toPdfValue.checked
        };
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/run",
            data: JSON.stringify(formDataV),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function(data) {
                if(data.pdfFilePath) {
                    downloadPdfFile(data.pdfFilePath);
                }
                $('.results').show();
                $('#textArea').html(data.textFormat);
                $('#latexArea').html(data.latexFormat);
                $(".errorDiv").hide();
                $(".spinner").hide();
            },
            error: function(e) {
                $('#textArea').html("");
                $('#latexArea').html("");
                $('.results').hide();
                $('.errorDiv').html(JSON.parse(e.responseText).message);
                $(".errorDiv").show();
                $(".spinner").hide();
            }
        });
    });
});

function downloadPdfFile(filePath) {
    let data = {
        'filePath': filePath,
    };

    $.ajax({
        url: '/api/download-pdf',
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(data),
        xhrFields: {
            responseType: 'blob'
        },
        success: function(blob) {
            let downloadUrl = URL.createObjectURL(blob);
            let a = document.createElement('a');
            a.href = downloadUrl;
            a.download = 'latex.pdf';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(downloadUrl);
        },
        error: function(xhr, status, error) {
            console.error("Dosya indirilirken bir hata oluştu:", error);
        }
    });
}

