/**
 * CareFlow Healthcare Management System - M3 Pharmacy UI Scripts
 * Vanilla JavaScript for Dispensing, Batch Selection, and Reports
 */

document.addEventListener("DOMContentLoaded", () => {
    initFefoAutoAlloc();
    initPurchaseOrderDynamicRows();
    initPrintButtons();
});

/**
 * Automatically allocates quantities across batches according to FEFO
 */
function initFefoAutoAlloc() {
    const autoAllocBtn = document.getElementById("autoAllocFefoBtn");
    const remainingQtyElem = document.getElementById("prescribedRemainingQty");

    if (!autoAllocBtn || !remainingQtyElem) return;

    autoAllocBtn.addEventListener("click", (e) => {
        e.preventDefault();
        let targetQty = parseInt(remainingQtyElem.getAttribute("data-remaining") || "0", 10);
        if (targetQty <= 0) return;

        const batchInputs = document.querySelectorAll(".batch-dispense-input");
        let needed = targetQty;

        batchInputs.forEach((input) => {
            const maxAvailable = parseInt(input.getAttribute("max") || "0", 10);
            if (needed > 0 && maxAvailable > 0) {
                const allocate = Math.min(needed, maxAvailable);
                input.value = allocate;
                needed -= allocate;
            } else {
                input.value = 0;
            }
        });

        updateDispenseSummary();
    });

    // Listen to changes on batch inputs to update live summary
    document.querySelectorAll(".batch-dispense-input").forEach((input) => {
        input.addEventListener("input", updateDispenseSummary);
    });
}

function updateDispenseSummary() {
    const batchInputs = document.querySelectorAll(".batch-dispense-input");
    let totalUnits = 0;
    let totalCost = 0.0;

    batchInputs.forEach((input) => {
        const qty = parseInt(input.value || "0", 10);
        const price = parseFloat(input.getAttribute("data-price") || "0.0");
        if (qty > 0) {
            totalUnits += qty;
            totalCost += (qty * price);
        }
    });

    const totalUnitsElem = document.getElementById("summaryTotalUnits");
    const totalCostElem = document.getElementById("summaryTotalCost");

    if (totalUnitsElem) totalUnitsElem.textContent = totalUnits;
    if (totalCostElem) totalCostElem.textContent = "$" + totalCost.toFixed(2);
}

/**
 * Dynamic row addition & removal for Purchase Orders
 */
function initPurchaseOrderDynamicRows() {
    const addRowBtn = document.getElementById("addPoItemRowBtn");
    const tableBody = document.getElementById("poItemsTableBody");

    if (!addRowBtn || !tableBody) return;

    addRowBtn.addEventListener("click", (e) => {
        e.preventDefault();
        const rowCount = tableBody.querySelectorAll("tr").length;
        const medicineSelectTemplate = document.getElementById("medicineSelectTemplate");

        if (!medicineSelectTemplate) return;

        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td>
                <select name="items[${rowCount}].medicineId" class="form-control po-medicine-select" required>
                    ${medicineSelectTemplate.innerHTML}
                </select>
            </td>
            <td>
                <input type="number" name="items[${rowCount}].orderedQuantity" class="form-control po-qty-input" min="1" value="10" required />
            </td>
            <td>
                <input type="number" step="0.01" name="items[${rowCount}].unitCost" class="form-control po-cost-input" min="0" value="0.00" required />
            </td>
            <td class="po-row-subtotal font-semibold text-right">$0.00</td>
            <td class="text-center">
                <button type="button" class="btn btn-outline btn-sm remove-row-btn" style="color:var(--danger);">✕</button>
            </td>
        `;

        tableBody.appendChild(newRow);
        bindPoRowEvents(newRow);
        updatePoTotals();
    });

    tableBody.querySelectorAll("tr").forEach(bindPoRowEvents);
    updatePoTotals();
}

function bindPoRowEvents(row) {
    const qtyInput = row.querySelector(".po-qty-input");
    const costInput = row.querySelector(".po-cost-input");
    const removeBtn = row.querySelector(".remove-row-btn");
    const selectElem = row.querySelector(".po-medicine-select");

    if (selectElem) {
        selectElem.addEventListener("change", () => {
            const selectedOpt = selectElem.options[selectElem.selectedIndex];
            const defaultCost = selectedOpt.getAttribute("data-cost");
            if (defaultCost && costInput) {
                costInput.value = parseFloat(defaultCost).toFixed(2);
            }
            updatePoTotals();
        });
    }

    if (qtyInput) qtyInput.addEventListener("input", updatePoTotals);
    if (costInput) costInput.addEventListener("input", updatePoTotals);
    if (removeBtn) {
        removeBtn.addEventListener("click", () => {
            const tableBody = document.getElementById("poItemsTableBody");
            if (tableBody && tableBody.querySelectorAll("tr").length > 1) {
                row.remove();
                reindexPoRows();
                updatePoTotals();
            } else {
                alert("Purchase order must have at least one item line.");
            }
        });
    }
}

function reindexPoRows() {
    const tableBody = document.getElementById("poItemsTableBody");
    if (!tableBody) return;
    const rows = tableBody.querySelectorAll("tr");
    rows.forEach((row, idx) => {
        const select = row.querySelector("select");
        const qty = row.querySelector(".po-qty-input");
        const cost = row.querySelector(".po-cost-input");
        if (select) select.name = `items[${idx}].medicineId`;
        if (qty) qty.name = `items[${idx}].orderedQuantity`;
        if (cost) cost.name = `items[${idx}].unitCost`;
    });
}

function updatePoTotals() {
    const rows = document.querySelectorAll("#poItemsTableBody tr");
    let grandTotal = 0.0;

    rows.forEach((row) => {
        const qtyInput = row.querySelector(".po-qty-input");
        const costInput = row.querySelector(".po-cost-input");
        const subtotalElem = row.querySelector(".po-row-subtotal");

        const qty = parseInt(qtyInput ? qtyInput.value : "0", 10) || 0;
        const cost = parseFloat(costInput ? costInput.value : "0.0") || 0.0;
        const subtotal = qty * cost;

        if (subtotalElem) {
            subtotalElem.textContent = "$" + subtotal.toFixed(2);
        }
        grandTotal += subtotal;
    });

    const grandTotalElem = document.getElementById("poGrandTotal");
    if (grandTotalElem) {
        grandTotalElem.textContent = "$" + grandTotal.toFixed(2);
    }
}

function initPrintButtons() {
    document.querySelectorAll(".btn-print").forEach((btn) => {
        btn.addEventListener("click", (e) => {
            e.preventDefault();
            window.print();
        });
    });
}
