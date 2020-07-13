const headers = {
  'Accept': 'application/json, text/plain, */*',
  'Content-Type': 'application/json'
};

let action;

const ACTION_STATES = {
  CREATE: 'CREATE',
  UPDATE: 'UPDATE',
  DELETE: 'DELETE'
};

const servicesContainer = $('#service-list');
const urlInput = $('#modal-url');
const nameInput = $('#modal-name');
const modalTitle = $('#modal-title');

$(document).ready(function() {
  populateServicesTable();
  setInterval(populateServicesTable, 5000);

  $('#create-modal-btn').click(function() {
    modalTitle.text('Create Service');
    nameInput.attr('disabled', false);
    nameInput.attr('placeholder', '');
    urlInput.attr('disabled', false);
    urlInput.attr('placeholder', '');
    urlInput.val('');
    nameInput.val('');
    action = ACTION_STATES.CREATE;
  });

  $('#action').click(function() {
    const url = urlInput.val();
    const name = nameInput.val();
    const urlPlaceHolder = urlInput.attr("placeholder");
    console.log(url);
    console.log(name);

    const method = {
      UPDATE: 'PATCH',
      CREATE: 'POST',
      DELETE: 'DELETE'
    }[action];

    const data = { url, name };
    if(action === ACTION_STATES.UPDATE ) data['urlToReplace'] = urlPlaceHolder;
    if(action === ACTION_STATES.DELETE) data['url'] = urlPlaceHolder;
   const options = {
     headers,
     method,
     body: JSON.stringify(data)
   };

    fetch('/service', options)
      .then(function(response) {
        console.log(response.status);
        if(response.status === 200) populateServicesTable()
      });

  });



});

const populateServicesTable = () => {
  servicesContainer.empty();
  let servicesRequest = new Request('/services');
  fetch(servicesRequest)
    .then(function(response) { return response.json(); })
    .then(function(serviceList) {
      console.log(serviceList);
      const table = $('<table>');
      table.addClass('table table-hover');
      const headers = ['Url', 'Name', 'Time Added', 'Status', 'Update', 'Delete'];
      const headerRow = $('<tr>');
      table.append(headerRow);
      headers.forEach(h => {
        const header = $('<th>');
        header.text(h);
        headerRow.append(header);
      });

      servicesContainer.append(table);

      serviceList.forEach(service => {
        const row = $('<tr>');

        const url = $('<td>');
        url.text(service.url);

        const name = $('<td>');
        name.text(service.name);

        const timeAdded = $('<td>');
        timeAdded.text(service.timeAdded);

        const status = $('<td>');
        status.text(service.status);
        if(service.status === 'OK') status.css('color','green');
        if(service.status === 'FAIL') status.css('color','red');

        const updateTableData = $('<td>');
        const updateBtn = $('<button>');
        updateBtn.text('Update');
        updateBtn.addClass('btn btn-outline-primary');
        updateBtn.attr('data-target', '#modal');
        updateBtn.attr('data-toggle', 'modal');
        updateBtn.attr('type','button');

        $(updateBtn).click(function() {
          modalTitle.text("Update Service");
          urlInput.val(service.url);
          nameInput.val(service.name);
          nameInput.attr('disabled', false);
          urlInput.attr('disabled', false);
          action = ACTION_STATES.UPDATE;
        });

        updateTableData.append(updateBtn);

        const deleteTableData = $('<td>');
        const deleteBtn = $('<button>');
        deleteBtn.text('Delete');
        deleteBtn.attr('data-target', '#modal');
        deleteBtn.attr('data-toggle', 'modal');
        deleteBtn.addClass('btn btn-outline-danger');

        $(deleteBtn).click(function() {
          modalTitle.text("Delete Service");
          urlInput.attr('placeholder', service.url);
          nameInput.attr('placeholder', service.name);
          nameInput.attr('disabled', true);
          urlInput.attr('disabled', true);
          action = ACTION_STATES.DELETE;
        });

        deleteTableData.append(deleteBtn);

        row.append(url);
        row.append(name);
        row.append(timeAdded);
        row.append(status);
        row.append(updateTableData);
        row.append(deleteTableData);

        table.append(row);

      });
    });
}