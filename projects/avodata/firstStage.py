import math
import numpy as np
import pandas as pd

# A program that receives a score matrix (c_matrix) for characteristics that characterize certain subjects.
# And produces a matrix M with size k*n (such that k is a characteristics of
# professions and n is the number of professions that are compared),
# that shows the relative value of the properties between the different professions
# The program using the Least Squares algorithm, the vector c with size k*1, and a
# matrix A with size (num_of_professions Choose 2) * (professions), to solve the equation Ax = c.
# The error matrix with size k*n is calculated by Ax - c = error.


def factorial(n):
    return int(math.gamma(n + 1))


def n_choose_k(n, k):
    return factorial(n) / (factorial(k) * factorial(n - k))


def generate_synthetic_a(num_of_professions):
    num_of_rows = int(n_choose_k(num_of_professions, 2))
    A = np.zeros(shape=(num_of_rows, num_of_professions), dtype=np.float32)
    row = 0

    for i in range(num_of_professions):
        for j in range(i + 1, num_of_professions):
            A[row][i] = 1
            A[row][j] = -1
            row += 1
    return A


def generate_synthetic_c(m, i, a):
    return np.dot(a, m[i].transpose())


def solve(A, c):
    return np.linalg.lstsq(A, c, None)[0]


def create_c_matrix_from_csv(file_name):
    # The structure of the csv file is as follows -
    # Each row represents a criterion for comparison (up and down),
    # and each column represents two subjects for comparison (Determined by matrix structure A)
    # In each slot, the score that led to a comparison between the two subjects that were compared
    # (according to the column) appears according to the criterion in the row
    data = pd.read_csv(file_name)
    return np.array(data)


def build_m(num_of_professions, c_matrix, num_of_characteristics):
    A = generate_synthetic_a(num_of_professions)
    m = np.zeros(
        (num_of_characteristics, num_of_professions), dtype=np.float32)
    for i in range(num_of_characteristics):
        c = c_matrix[i]
        row_in_m = solve(A, c)
        # We normalize to the[0, 1] range:
        row_in_m = (row_in_m - np.min(row_in_m))
        row_in_m /= np.max(row_in_m)
        m[i] = row_in_m
    return m


def run_check(m):
    num_of_professions = m.shape[1]
    A = generate_synthetic_a(num_of_professions)

    num_of_rows_in_m = m.shape[0]
    new_m = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
    c_matrix = np.zeros(
        (num_of_rows_in_m, num_of_professions), dtype=np.float32)
    for i in range(num_of_rows_in_m):
        c = generate_synthetic_c(m, i, A)
        c_matrix[i] = c
        row_in_m = solve(A, c)
        new_m[i] = row_in_m
    Ax = np.dot(A, new_m.transpose())
    errors = np.subtract(Ax, c_matrix.transpose())

    return new_m, errors


# This is an example of running the algorithm.
# The professions are - driver, lawyer, programmer
# The characteristics are - social status, working with people, analytical thinking
# The extracted matrix from the csv file is -
# c_matrix = [[-3, -3, -2],
#             [-1, 2, 3],
#             [-3, -3, -2]]

c_matrix = create_c_matrix_from_csv('check.csv')
print(c_matrix)
m = build_m(3, c_matrix, 3)
print(m)
