let elements = [];

$(document).ready(function() {
    elements = [
        {value: 0.70710678118},
        {value: 0.70781353429},
        {value: 0.70851957959},
        {value: 0.70922491637},
    ];

    loadData();

    $(".spinner").hide();
    $(".results").hide();
    $('.errorDiv').hide();
    $("#btn").click(function(event) {
        event.preventDefault();
        $(".spinner").show();
        //let v = document.getElementById("v");
        let d = document.getElementById("d");
        let mTxt = document.getElementById("mTxt");
        let hVal = document.getElementById("hVal");
        let toTextValue = document.getElementById("toTextValue");
        let toPdfValue = document.getElementById("toPdfValue");

        const yValues = [];
        for(let i=0; i<elements.length; i++) {
            yValues.push(elements[i].value)
        }


        let formDataV = {
            'd': d.value,
            //'v': v.value,
            'm': mTxt.value,
            'h': hVal.value,
            'yvalues': yValues,
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
            a.download = 'f_diff_latex.pdf';
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

function appendData() {
    let input = document.getElementById("fValues");
    elements.push({value: input.value}); input.value = ""; loadData();
}

function loadData() {
    removeElementsByClass("yDataItem");
    let ul = document.getElementById("yDataList");
    for(let i=0; i<elements.length; i++) {
        let li = document.createElement("li");
        li.id = "yDataItem-"+i;
        li.className = "list-group-item d-flex justify-content-between align-items-start yDataItem";
        li.appendChild(document.createTextNode("y"+i + " - " + elements[i].value));

        let a  = document.createElement("a");
        a.className = "badge bg-primary rounded-pill text-decoration-none";
        a.role= "button";
        a.textContent = "X";
        a.onclick = function () {
            elements.splice(i ,1);
            loadData();
        };
        li.appendChild(a);
        ul.appendChild(li);
    }
}

function removeElementsByClass(className){
    const elements = document.getElementsByClassName(className);
    while(elements.length > 0){
        elements[0].parentNode.removeChild(elements[0]);
    }
}
