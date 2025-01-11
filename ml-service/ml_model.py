from flask import Flask, request, jsonify
import numpy as np
from sklearn.linear_model import LinearRegression

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json['transactions']

    # Prepare data for linear regression
    days = np.arange(len(data)).reshape(-1, 1)
    amounts = np.array([transaction['amount'] for transaction in data]).reshape(-1, 1)

    # Train the model
    model = LinearRegression()
    model.fit(days, amounts)

    # Predict the next day's spending
    next_day = np.array([[len(data)]])
    predicted_amount = model.predict(next_day)[0][0]

    return jsonify({"predicted_amount": predicted_amount})

if __name__ == '__main__':
    app.run(debug=True)